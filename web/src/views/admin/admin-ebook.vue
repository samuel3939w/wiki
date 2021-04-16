<template>
    <a-layout-content
            :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
        <p>
            <a-form layout="inline" :model="param">
                <a-form-item>
                    <a-input v-model:value="param.name" placeholder="名稱">
                    </a-input>
                </a-form-item>
                <a-form-item>
                    <a-button type="primary" @click="handleQuery({page:1,size:pagination.pageSize})">
                        查詢
                    </a-button>
                </a-form-item>
                <a-form-item>
                    <a-button type="primary" @click="add()">
                        新增
                    </a-button>
                </a-form-item>
            </a-form>
        </p>
        <a-table
                :columns="columns"
                :row-key="record => record.id"
                :data-source="ebooks"
                :pagination="pagination"
                :loading="loading"
                @change="handleTableChange"
        >
            <template #cover="{ text:cover }">
                <img v-if="cover" :src="cover" alt="avatar"/>
            </template>
            <template v-slot:category="{ text, record }">
                <span>{{ getCategoryName(record.category1Id) }} / {{ getCategoryName(record.category2Id) }}</span>
            </template>
            <template v-slot:action="{ text, record }">
                <a-space size="small">
                    <router-link :to="'/admin/doc?ebookId='+record.id">
                        <a-button type="primary">
                            文檔管理
                        </a-button>
                    </router-link>

                    <a-button type="primary" @click="edit(record)">
                        编辑
                    </a-button>
                    <a-popconfirm
                            title="刪除後不可恢復，確認刪除?"
                            ok-text="是"
                            cancel-text="否"
                            @confirm="handleDelete(record.id)"
                    >
                        <a-button type="danger">
                            删除
                        </a-button>
                    </a-popconfirm>
                </a-space>
            </template>
        </a-table>
    </a-layout-content>

    <a-modal
            title="電子書列表"
            v-model:visible="modalVisible"
            :confirm-loading="modalLoading"
            @ok="handleModalOk"
    >
        <a-form :model="ebook" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
            <a-form-item label="封面">
                <a-input v-model:value="ebook.cover"/>
            </a-form-item>
            <a-form-item label="名稱">
                <a-input v-model:value="ebook.name"/>
            </a-form-item>
            <a-form-item label="分類">
                <a-cascader v-model:value="categoryIds"
                            :field-names="{label:'name',value:'id',children:'children'}"
                            :options="level1"
                />
            </a-form-item>
            <a-form-item label="描述">
                <a-input v-model:value="ebook.description" type="textarea"/>
            </a-form-item>
        </a-form>
    </a-modal>
</template>

<script lang="ts">
    import {defineComponent, onMounted, ref} from 'vue';
    import axios from 'axios';
    import {message} from 'ant-design-vue';
    import {Tool} from '@/utils/tool';

    export default defineComponent({
        name: 'AdminEbook',
        setup() {
            const param = ref();
            param.value = {};
            const ebooks = ref();
            const pagination = ref({
                current: 1,
                pageSize: 4,
                total: 0
            });
            const loading = ref(false);

            const columns = [
                {
                    title: '封面',
                    dataIndex: 'cover',
                    slots: {customRender: 'cover'}
                },
                {
                    title: '名稱',
                    dataIndex: 'name'
                },
                {
                    title: '分类',
                    slots: {customRender: 'category'}
                },
                {
                    title: '文檔數',
                    dataIndex: 'docCount'
                },
                {
                    title: '閱讀數',
                    dataIndex: 'viewCount'
                },
                {
                    title: '點讚數',
                    dataIndex: 'voteCount'
                },
                {
                    title: 'Action',
                    key: 'action',
                    slots: {customRender: 'action'}
                }
            ];

            /**
             * 数据查询
             **/
            const handleQuery = (params: any) => {
                loading.value = true;
                axios.get("/ebook/list", {
                    params: {
                        page: params.page,
                        size: params.size,
                        name: param.value.name
                    }
                }).then((response) => {
                    loading.value = false;
                    const data = response.data;
                    if (data.success) {
                        ebooks.value = data.content.list;

                        // 重置分页按钮
                        pagination.value.current = params.page;
                        pagination.value.total = data.content.total;
                    } else {
                        message.error(data.message);
                    }

                });
            };

            /**
             * 表格点击页码时触发
             */
            const handleTableChange = (pagination: any) => {
                console.log("看看自帶的分頁參數都有啥：" + pagination);
                handleQuery({
                    page: pagination.current,
                    size: pagination.pageSize
                });
            };

            // -------- 表单 ---------
            /**
             * 数组，[100, 101]对应：前端开发 / Vue
             */
            const categoryIds = ref();
            const ebook = ref();
            const modalVisible = ref(false);
            const modalLoading = ref(false);
            const handleModalOk = () => {
                modalLoading.value = true;
                ebook.value.category1Id = categoryIds.value[0];
                ebook.value.category2Id = categoryIds.value[1];
                axios.post("/ebook/save", ebook.value).then((response) => {
                    modalLoading.value = false;
                    const data = response.data;
                    if (data.success) {
                        modalVisible.value = false;

                        //重新加載列表
                        handleQuery({
                            page: pagination.value.current,
                            size: pagination.value.pageSize
                        });
                    } else {
                        message.error(data.message);
                    }
                });
            };

            /**
             * 编辑
             */
            const edit = (record: any) => {
                modalVisible.value = true;
                ebook.value = Tool.copy(record);
                categoryIds.value = [ebook.value.category1Id, ebook.value.category2Id];
            };

            /**
             * 新增
             */
            const add = () => {
                modalVisible.value = true;
                ebook.value = {}
            };

            const level1 = ref();
            let categorys: any;
            /**
             * 查询所有分类
             **/
            const handleQueryCategory = () => {
                loading.value = true;
                axios.get("/category/all").then((response) => {
                    loading.value = false;
                    const data = response.data;
                    if (data.success) {
                        categorys = data.content;
                        console.log("原始数组：", categorys);

                        level1.value = [];
                        level1.value = Tool.array2Tree(categorys, 0);
                        console.log("树形结构：", level1.value);

                        //加載完分類後,再加載電子書,否則如果分類樹加載很慢,則電子書渲染會報錯
                        handleQuery({
                            page: 1,
                            size: pagination.value.pageSize
                        });
                    } else {
                        message.error(data.message);
                    }
                });
            };

            const getCategoryName = (cid: number) => {
                // console.log(cid)
                let result = "";
                categorys.forEach((item: any) => {
                    if (item.id === cid) {
                        // return item.name; // 注意，这里直接return不起作用
                        result = item.name;
                    }
                });
                return result;
            };

            const handleDelete = (id: number) => {
                console.log(typeof id);
                axios.delete("/ebook/delete/" + id).then((response) => {
                    const data = response.data;
                    if (data.success) {
                        //重新加載列表
                        handleQuery({
                            page: pagination.value.current,
                            size: pagination.value.pageSize
                        });
                    }
                });
            }

            onMounted(() => {
                handleQueryCategory();
            });

            return {
                param,
                ebooks,
                pagination,
                columns,
                loading,
                handleTableChange,

                edit,
                add,

                ebook,
                modalVisible,
                modalLoading,
                handleModalOk,
                categoryIds,
                level1,

                handleDelete,
                handleQuery,
                getCategoryName
            }
        }
    });
</script>

<style scoped>
    img {
        width: 50px;
        height: 50px;
    }
</style>
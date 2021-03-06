<template>
    <a-layout-content
            :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
        <p>
            <a-form layout="inline" :model="param">
                <a-form-item>
                    <a-input v-model:value="param.name" placeholder="類別名稱">
                    </a-input>
                </a-form-item>
                <a-form-item>
                    <a-button type="primary" @click="handleQuery()">
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
                :data-source="level1"
                :loading="loading"
                :pagination="false"
        >
            <template #cover="{ text: cover }">
                <img v-if="cover" :src="cover" alt="avatar"/>
            </template>
            <template v-slot:action="{ text, record }">
                <a-space size="small">
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
            title="分類列表"
            v-model:visible="modalVisible"
            :confirm-loading="modalLoading"
            @ok="handleModalOk({
               name:category.name,
               sort:category.sort,
               parent:category.parent
            })"
    >
        <a-form :model="category" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
            <a-form-item label="名稱">
                <a-input v-model:value="category.name"/>
            </a-form-item>
            <a-form-item label="父分類">
                <a-select
                        v-model:value="category.parent"
                        ref="select"
                >
                    <a-select-option value="0">無</a-select-option>
                    <a-select-option v-for="c in level1" :key="c.id" :value="c.id" :disabled="category.id === c.id">
                        {{c.name}}
                    </a-select-option>
                </a-select>
            </a-form-item>
            <a-form-item label="順序">
                <a-input v-model:value="category.sort"/>
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
        name: 'AdminCategory',
        setup() {
            const param = ref();
            param.value = {};
            const categorys = ref();
            const loading = ref(false);

            const columns = [
                {
                    title: '名稱',
                    dataIndex: 'name'
                },
                {
                    title: '父分類',
                    key: 'parent',
                    dataIndex: 'parent'
                },
                {
                    title: '順序',
                    dataIndex: 'sort'
                },
                {
                    title: 'Action',
                    key: 'action',
                    slots: {customRender: 'action'}
                }
            ];

            /**
             * 一级分类树，children属性就是二级分类
             * [{
             *   id: "",
             *   name: "",
             *   children: [{
             *     id: "",
             *     name: "",
             *   }]
             * }]
             */
            const level1 = ref(); // 一级分类树，children属性就是二级分类
            level1.value = [];

            /**
             * 数据查询
             **/
            const handleQuery = () => {
                loading.value = true;
                //判斷name是否有值,有值則用第一個請求(不使用樹狀結構),沒有值則使用第二個請求
                if (param.value.name) {
                    axios.get("/category/all", {
                        params: {
                            name: param.value.name
                        }
                    }).then((response) => {
                        loading.value = false;
                        const data = response.data;
                        if (data.success) {
                            level1.value = data.content;
                        } else {
                            message.error(data.message);
                        }
                    });
                } else {
                    axios.get("/category/all", {
                        params: {
                            name: param.value.name
                        }
                    }).then((response) => {
                        loading.value = false;
                        const data = response.data;
                        if (data.success) {
                            categorys.value = data.content;
                            console.log("原始數據:", categorys.value);

                            level1.value = Tool.array2Tree(categorys.value, 0);
                            console.log("樹狀結構:", level1);
                        } else {
                            message.error(data.message);
                        }
                    });
                }

            };

            // -------- 表单 ---------
            const category = ref({});
            const modalVisible = ref(false);
            const modalLoading = ref(false);
            const handleModalOk = (param: any) => {
                modalLoading.value = true;

                //驗證提交內容
                let name = param.name;
                const parent = parseInt(param.parent);
                const sort = parseInt(param.sort);
                if (name == "" || name == undefined) {
                    message.error("名稱不可為空!");
                    modalLoading.value = false;
                    return;
                } else if (!Number.isInteger(parent)) {
                    message.error("父分類只能輸入數字!");
                    modalLoading.value = false;
                    return;
                } else if (!Number.isInteger(sort)) {
                    message.error("排序只能輸入數字!");
                    modalLoading.value = false;
                    return;
                }

                axios.post("/category/save", category.value).then((response) => {
                    modalLoading.value = false;
                    const data = response.data;
                    if (data.success) {
                        modalVisible.value = false;

                        //重新加載列表
                        handleQuery();
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
                category.value = Tool.copy(record);
            };

            /**
             * 新增
             */
            const add = () => {
                modalVisible.value = true;
                category.value = {}
            };

            const handleDelete = (id: number) => {
                console.log(typeof id);
                axios.delete("/category/delete/" + id).then((response) => {
                    const data = response.data;
                    if (data.success) {
                        //重新加載列表
                        handleQuery();
                    }
                });
            }

            onMounted(() => {
                handleQuery();
            });

            return {
                param,
                //categorys,
                level1,
                columns,
                loading,

                edit,
                add,

                category,
                modalVisible,
                modalLoading,
                handleModalOk,

                handleDelete,
                handleQuery
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
<template>
    <a-layout-content
            :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
        <p>
            <a-form layout="inline" :model="param">
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
            title="文檔列表"
            v-model:visible="modalVisible"
            :confirm-loading="modalLoading"
            @ok="handleModalOk({
               name:doc.name,
               sort:doc.sort,
               parent:doc.parent
            })"
    >
        <a-form :model="doc" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
            <a-form-item label="名稱">
                <a-input v-model:value="doc.name"/>
            </a-form-item>
            <a-form-item label="父文檔">
                <a-tree-select
                        v-model:value="doc.parent"
                        style="width: 100%"
                        :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
                        :tree-data="treeSelectData"
                        placeholder="請選擇父文檔"
                        tree-default-expand-all
                        :replaceFields="{title:'name',key:'id',value:'id'}"
                >
                </a-tree-select>
            </a-form-item>
            <a-form-item label="順序">
                <a-input v-model:value="doc.sort"/>
            </a-form-item>
        </a-form>
    </a-modal>
</template>

<script lang="ts">
    import {defineComponent, onMounted, ref, createVNode} from 'vue';
    import axios from 'axios';
    import {message, Modal} from 'ant-design-vue';
    import {Tool} from '@/utils/tool';
    import {useRoute} from "vue-router";
    import ExclamationCircleOutlined from "@ant-design/icons-vue/ExclamationCircleOutlined";

    export default defineComponent({
        name: 'AdminDoc',
        setup() {
            const route = useRoute();
            const param = ref();
            param.value = {};
            const docs = ref();
            const loading = ref(false);

            const columns = [
                {
                    title: '名稱',
                    dataIndex: 'name'
                },
                {
                    title: '父文檔',
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
             * 一级文檔树，children属性就是二级文檔
             * [{
             *   id: "",
             *   name: "",
             *   children: [{
             *     id: "",
             *     name: "",
             *   }]
             * }]
             */
            const level1 = ref(); // 一级文檔树，children属性就是二级文檔
            level1.value = [];

            /**
             * 数据查询
             **/
            const handleQuery = () => {
                loading.value = true;
                //判斷name是否有值,有值則用第一個請求(不使用樹狀結構),沒有值則使用第二個請求
                if (param.value.name) {
                    axios.get("/doc/all", {
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
                    axios.get("/doc/all", {
                        params: {
                            name: param.value.name
                        }
                    }).then((response) => {
                        loading.value = false;
                        const data = response.data;
                        if (data.success) {
                            docs.value = data.content;
                            console.log("原始數據:", docs.value);

                            level1.value = Tool.array2Tree(docs.value, 0);
                            console.log("樹狀結構:", level1);
                        } else {
                            message.error(data.message);
                        }
                    });
                }

            };

            // -------- 表单 ---------
            // 因为树选择组件的属性状态，会随当前编辑的节点而变化，所以单独声明一个响应式变量
            const treeSelectData = ref();
            treeSelectData.value = [];
            const doc = ref({});
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
                    message.error("父文檔只能輸入數字!");
                    modalLoading.value = false;
                    return;
                } else if (!Number.isInteger(sort)) {
                    message.error("排序只能輸入數字!");
                    modalLoading.value = false;
                    return;
                }

                axios.post("/doc/save", doc.value).then((response) => {
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
             * 将某节点及其子孙节点全部置为disabled
             */
            const setDisable = (treeSelectData: any, id: any) => {
                // console.log(treeSelectData, id);
                // 遍历数组，即遍历某一层节点
                for (let i = 0; i < treeSelectData.length; i++) {
                    const node = treeSelectData[i];
                    if (node.id === id) {
                        // 如果当前节点就是目标节点
                        console.log("disabled", node);
                        // 将目标节点设置为disabled
                        node.disabled = true;

                        // 遍历所有子节点，将所有子节点全部都加上disabled
                        const children = node.children;
                        if (Tool.isNotEmpty(children)) {
                            for (let j = 0; j < children.length; j++) {
                                setDisable(children, children[j].id)
                            }
                        }
                    } else {
                        // 如果当前节点不是目标节点，则到其子节点再找找看。
                        const children = node.children;
                        if (Tool.isNotEmpty(children)) {
                            setDisable(children, id);
                        }
                    }
                }
            };

            const deleteIds: Array<string> = [];
            const deleteNames: Array<string> = [];
            /**
             * 查找整根樹枝
             */
            const getDeleteIds = (treeSelectData: any, id: any) => {
                // console.log(treeSelectData, id);
                // 遍历数组，即遍历某一层节点
                for (let i = 0; i < treeSelectData.length; i++) {
                    const node = treeSelectData[i];
                    if (node.id === id) {
                        // 如果当前节点就是目标节点
                        console.log("delete", node);
                        // 將目標ID放入結果集ids
                        //node.disabled = true;
                        deleteIds.push(id);
                        deleteNames.push(node.name);

                        // 遍历所有子节点
                        const children = node.children;
                        if (Tool.isNotEmpty(children)) {
                            for (let j = 0; j < children.length; j++) {
                                getDeleteIds(children, children[j].id)
                            }
                        }
                    } else {
                        // 如果当前节点不是目标节点，则到其子节点再找找看。
                        const children = node.children;
                        if (Tool.isNotEmpty(children)) {
                            getDeleteIds(children, id);
                        }
                    }
                }
            };
            /**
             * 编辑
             */
            const edit = (record: any) => {
                modalVisible.value = true;
                doc.value = Tool.copy(record);

                // 不能选择当前节点及其所有子孙节点，作为父节点，会使树断开
                treeSelectData.value = Tool.copy(level1.value);
                setDisable(treeSelectData.value, record.id);

                // 为选择树添加一个"无"
                treeSelectData.value.unshift({id: 0, name: '无'});
            };

            /**
             * 新增
             */
            const add = () => {
                modalVisible.value = true;
                doc.value = {
                    ebookId: route.query.ebookId
                }

                treeSelectData.value = Tool.copy(level1.value);

                // 为选择树添加一个"无"
                treeSelectData.value.unshift({id: 0, name: '无'});
            };

            const handleDelete = (id: number) => {
                getDeleteIds(level1.value, id);
                if (deleteIds.length === 0 || deleteNames.length === 0) {
                    message.error("刪除的文檔ID或文檔名為空!");
                    return
                }
                Modal.confirm({
                    title: '重要提醒',
                    icon: createVNode(ExclamationCircleOutlined),
                    content: '將刪除：【' + deleteNames.join("，") + "】刪除後不可恢復，確認刪除？",
                    onOk() {
                        // console.log(ids)
                        axios.delete("/doc/delete/" + deleteIds.join(",")).then((response) => {
                            const data = response.data; // data = commonResp
                            if (data.success) {
                                // 重新加载列表
                                handleQuery();
                            }
                        });
                    },
                });
            }

            onMounted(() => {
                handleQuery();
            });

            return {
                param,
                //docs,
                level1,
                columns,
                loading,

                edit,
                add,

                doc,
                modalVisible,
                modalLoading,
                handleModalOk,

                handleDelete,
                handleQuery,

                treeSelectData
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
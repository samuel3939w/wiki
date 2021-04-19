<template>
    <a-layout-content
            :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
        <a-row :gutter="24">
            <a-col :span="8">
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
                        v-if="level1.length > 0"
                        :columns="columns"
                        :row-key="record => record.id"
                        :data-source="level1"
                        :loading="loading"
                        :pagination="false"
                        size="small"
                        :defaultExpandAllRows="true"
                >
                    <template #name="{ text,record }">
                        {{record.sort}} {{text}}
                    </template>
                    <template v-slot:action="{ text, record }">
                        <a-space size="small">
                            <a-button type="primary" @click="edit(record)" size="small">
                                编辑
                            </a-button>
                            <a-popconfirm
                                    title="刪除後不可恢復，確認刪除?"
                                    ok-text="是"
                                    cancel-text="否"
                                    @confirm="handleDelete(record.id)"
                            >
                                <a-button type="danger" size="small">
                                    删除
                                </a-button>
                            </a-popconfirm>
                        </a-space>
                    </template>
                </a-table>
            </a-col>
            <a-col :span="16">
                <p>
                    <a-form layout="inline">
                        <a-form-item>
                            <a-button type="primary" @click="handleSave()">
                                保存
                            </a-button>
                        </a-form-item>
                    </a-form>
                </p>
                <a-form :model="doc" layout="vertical">
                    <a-form-item>
                        <a-input v-model:value="doc.name" placeholder="名稱"/>
                    </a-form-item>
                    <a-form-item>
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
                    <a-form-item>
                        <a-input v-model:value="doc.sort" placeholder="順序"/>
                    </a-form-item>
                    <a-form-item>
                        <div id="content"></div>
                    </a-form-item>
                </a-form>
            </a-col>
        </a-row>
    </a-layout-content>

    <!--    <a-modal-->
    <!--            title="文檔列表"-->
    <!--            v-model:visible="modalVisible"-->
    <!--            :confirm-loading="modalLoading"-->
    <!--            @ok="handleModalOk({-->
    <!--               name:doc.name,-->
    <!--               sort:doc.sort,-->
    <!--               parent:doc.parent-->
    <!--            })"-->
    <!--    >-->

    <!--    </a-modal>-->
</template>

<script lang="ts">
    import {defineComponent, onMounted, ref, createVNode} from 'vue';
    import axios from 'axios';
    import {message, Modal} from 'ant-design-vue';
    import {Tool} from '@/utils/tool';
    import {useRoute} from "vue-router";
    import ExclamationCircleOutlined from "@ant-design/icons-vue/ExclamationCircleOutlined";
    import E from "wangeditor";

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
                    //dataIndex對應的是模板裡面text的值
                    dataIndex: 'name',
                    slots: {customRender: 'name'}
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

                axios.get("/doc/all").then((response) => {
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
            };

            // -------- 表单 ---------
            // 因为树选择组件的属性状态，会随当前编辑的节点而变化，所以单独声明一个响应式变量
            const treeSelectData = ref();
            treeSelectData.value = [];
            const doc = ref();
            doc.value={};
            const modalVisible = ref(false);
            const modalLoading = ref(false);
            const editor = new E('#content');
            editor.config.zIndex = 0;

            const handleSave = () => {
                modalLoading.value = true;
                doc.value.content = editor.txt.html();
                axios.post("/doc/save", doc.value).then((response) => {
                    modalLoading.value = false;
                    const data = response.data; // data = commonResp
                    if (data.success) {
                        // modalVisible.value = false;
                        message.success("保存成功！");

                        // 重新加载列表
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
             * 內容查询
             **/
            const handleQueryContent = () => {
                axios.get("/doc/find-content/"+doc.value.id).then((response) => {
                    const data = response.data;
                    if (data.success) {
                        editor.txt.html(data.content)
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
                doc.value = Tool.copy(record);
                handleQueryContent();

                // 不能选择当前节点及其所有子孙节点，作为父节点，会使树断开
                treeSelectData.value = Tool.copy(level1.value);
                setDisable(treeSelectData.value, record.id);

                // 为选择树添加一个"无"
                treeSelectData.value.unshift({id: 0, name: '無'});
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
                treeSelectData.value.unshift({id: 0, name: '無'});
            };

            const handleDelete = (id: number) => {
                // 清空數組，否則多次刪除時，數組會一直增加
                deleteIds.length = 0;
                deleteNames.length = 0;
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
                editor.create();
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
                handleSave,

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
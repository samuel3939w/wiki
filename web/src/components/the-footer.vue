<template>
    <a-layout-footer style="text-align: center">
        山姆電子書<span v-show="user.id">, 歡迎: {{user.name}}</span>
    </a-layout-footer>
</template>

<script lang="ts">
    import {defineComponent, computed, onMounted} from 'vue';
    import store from "@/store";
    import {Tool} from "@/utils/tool";
    import {notification} from 'ant-design-vue';

    export default defineComponent({
        name: 'the-footer',
        setup() {
            const user = computed(() => store.state.user);

            let websocket: any;
            let token: any;

            const onOpen = () => {
                console.log('WebSocket連接成功，狀態碼：', websocket.readyState)
            };
            const onMessage = (event: any) => {
                console.log('WebSocket收到消息：', event.data);
                notification["info"]({
                    message:'收到消息',
                    description:event.data
                })
            };
            const onError = () => {
                console.log('WebSocket連接錯誤，狀態碼：', websocket.readyState)
            };
            const onClose = () => {
                console.log('WebSocket連接關閉，狀態碼：', websocket.readyState)
            };
            const initWebSocket = () => {
                // 連接成功
                websocket.onopen = onOpen;
                // 收到消息的回調
                websocket.onmessage = onMessage;
                // 連接錯誤
                websocket.onerror = onError;
                // 連接關閉的回調
                websocket.onclose = onClose;
            };

            onMounted(() => {
                // WebSocket
                if ('WebSocket' in window) {
                    token = Tool.uuid(10);
                    // 连接地址：ws://127.0.0.1:8880/ws/xxx
                    websocket = new WebSocket(process.env.VUE_APP_WS_SERVER + '/ws/' + token);
                    initWebSocket()

                    // 关闭
                    // websocket.close();
                } else {
                    alert('當前瀏覽器 不支持')
                }
            });

            return {
                user
            }
        }
    });
</script>
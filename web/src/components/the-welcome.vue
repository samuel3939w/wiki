<template>
    <div>
        <a-row>
            <a-col :span="24">
                <a-card>
                    <a-row>
                        <a-col :span="8">
                            <a-statistic title="總閱讀量" :value="statistic.viewCount">
                                <template #suffix>
                                    <UserOutlined/>
                                </template>
                            </a-statistic>
                        </a-col>
                        <a-col :span="8">
                            <a-statistic title="總點讚量" :value="statistic.voteCount">
                                <template #suffix>
                                    <like-outlined/>
                                </template>
                            </a-statistic>
                        </a-col>
                        <a-col :span="8">
                            <a-statistic title="點讚率" :value="statistic.voteCount / statistic.viewCount * 100"
                                         :precision="2"
                                         suffix="%"
                                         :value-style="{ color: '#cf1322' }">
                                <template #suffix>
                                    <like-outlined/>
                                </template>
                            </a-statistic>
                        </a-col>
                    </a-row>
                </a-card>
            </a-col>
        </a-row>
        <br>
        <a-row :gutter="16">
            <a-col :span="12">
                <a-card>
                    <a-row>
                        <a-col :span="12">
                            <a-statistic title="今日閱讀" :value="statistic.todayViewCount" style="margin-right: 50px">
                                <template #suffix>
                                    <UserOutlined/>
                                </template>
                            </a-statistic>
                        </a-col>
                        <a-col :span="12">
                            <a-statistic title="今日點讚" :value="statistic.todayVoteCount">
                                <template #suffix>
                                    <like-outlined/>
                                </template>
                            </a-statistic>
                        </a-col>
                    </a-row>
                </a-card>
            </a-col>
            <a-col :span="12">
                <a-card>
                    <a-row>
                        <a-col :span="12">
                            <a-statistic
                                    title="預計今日閱讀"
                                    :value="statistic.todayViewIncrease"
                                    :value-style="{ color: '#0000ff' }"
                            >
                                <template #suffix>
                                    <UserOutlined/>
                                </template>
                            </a-statistic>
                        </a-col>
                        <a-col :span="12">
                            <a-statistic
                                    title="預計今日閱讀增長"
                                    :value="statistic.todayViewIncreaseRateAbs"
                                    :precision="2"
                                    suffix="%"
                                    class="demo-class"
                                    :value-style="statistic.todayViewIncreaseRate < 0 ? { color: '#3f8600' } : { color: '#cf1322' }"
                            >
                                <template #prefix>
                                    <arrow-down-outlined v-if="statistic.todayViewIncreaseRate < 0"/>
                                    <arrow-up-outlined v-if="statistic.todayViewIncreaseRate >= 0"/>
                                </template>
                            </a-statistic>
                        </a-col>
                    </a-row>
                </a-card>
            </a-col>
        </a-row>
        <br>
        <a-row>
            <a-col :span="24" id="main-col">
                <div id="main" style="width: 100%;height:300px;"></div>
            </a-col>
        </a-row>
    </div>
</template>

<script lang="ts">
    import {defineComponent, ref, onMounted} from 'vue'
    import axios from 'axios';

    declare let echarts: any;

    export default defineComponent({
        name: 'the-welcome',
        setup() {
            const statistic = ref();
            statistic.value = {};

            const getStatistic = () => {
                axios.get('/ebook-snapshot/get-statistic').then((response) => {
                    const data = response.data;
                    if (data.success) {
                        const statisticResp = data.content;
                        statistic.value.viewCount = statisticResp[1].viewCount;
                        statistic.value.voteCount = statisticResp[1].voteCount;
                        statistic.value.todayViewCount = statisticResp[1].viewIncrease;
                        statistic.value.todayVoteCount = statisticResp[1].voteIncrease;

                        // 按分鐘計算當前時間點，佔一天的百分比
                        const now = new Date();
                        const nowRate = (now.getHours() * 60 + now.getMinutes()) / (60 * 24);
                        // console.log(nowRate)
                        statistic.value.todayViewIncrease = parseInt(String(statisticResp[1].viewIncrease / nowRate));
                        // todayViewIncreaseRate：今日預計增長率
                        statistic.value.todayViewIncreaseRate = (statistic.value.todayViewIncrease - statisticResp[0].viewIncrease) / statisticResp[0].viewIncrease * 100;
                        statistic.value.todayViewIncreaseRateAbs = Math.abs(statistic.value.todayViewIncreaseRate);
                    }
                });
            };


            const init30DayEcharts = (list: any) => {
                // 發布生產後出現問題：切到別的頁面，再切回首頁，報表顯示不出來
                // 解决方法：把原来的id=main的區域清空，重新初始化
                const mainDom = document.getElementById('main-col');
                if (mainDom) {
                    mainDom.innerHTML = '<div id="main" style="width: 100%;height:300px;"></div>';
                }
                // 基于准备好的dom，初始化echarts實例
                const myChart = echarts.init(document.getElementById('main'));

                const xAxis = [];
                const seriesView = [];
                const seriesVote = [];
                for (let i = 0; i < list.length; i++) {
                    const record = list[i];
                    xAxis.push(record.date);
                    seriesView.push(record.viewIncrease);
                    seriesVote.push(record.voteIncrease);
                }

                // 指定圖表的配置項和數據
                const option = {
                    title: {
                        text: '30天趨勢圖'
                    },
                    tooltip: {
                        trigger: 'axis'
                    },
                    legend: {
                        data: ['總閱讀量', '總點讚量']
                    },
                    grid: {
                        left: '1%',
                        right: '3%',
                        bottom: '3%',
                        containLabel: true
                    },
                    toolbox: {
                        feature: {
                            saveAsImage: {}
                        }
                    },
                    xAxis: {
                        type: 'category',
                        boundaryGap: false,
                        data: xAxis
                    },
                    yAxis: {
                        type: 'value'
                    },
                    series: [
                        {
                            name: '總閱讀量',
                            type: 'line',
                            // stack: '總量', 不堆疊
                            data: seriesView,
                            smooth: true
                        },
                        {
                            name: '總點讚量',
                            type: 'line',
                            // stack: '總量', 不堆叠
                            data: seriesVote,
                            smooth: true
                        }
                    ]
                };

                // 使用剛指定的配置項和數據顯示圖表。
                myChart.setOption(option);
            };

            const get30DayStatistic = () => {
                axios.get('/ebook-snapshot/get-30-statistic').then((response) => {
                    const data = response.data;
                    if (data.success) {
                        const statisticList = data.content;

                        init30DayEcharts(statisticList)
                    }
                });
            };

            onMounted(() => {
                getStatistic();
                get30DayStatistic();
            });

            return {
                statistic
            }
        }
    });
</script>
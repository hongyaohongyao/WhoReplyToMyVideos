<template>
    <VePie :data="chartData" :settings="chartSettings"
           height="350px"
           align="center"
           :extend="chartExtend"></VePie>
</template>

<script>
    import VePie from 'v-charts/lib/pie.common'

    export default {
        name: "LevelCountPie",
        data() {
            return {
                chartSettings: {roseType: 'radius'},
                chartExtend: {
                    series: {
                        center: ['50%', '50%'],
                    }
                }
            }
        },
        props: ['datas'],
        computed: {
            chartData() {
                return {
                    columns: ['等级', '人数'],
                    rows: (this.datas || [])
                }
            }
        },
        components: {VePie}
    }
</script>

<style scoped>
</style>
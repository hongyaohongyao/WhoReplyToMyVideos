<template>

    <VePie :data="chartData" :settings="chartSettings"
           height="300px"
           align="center"
           :extend="chartExtend"></VePie>


</template>
<script>
    import VePie from 'v-charts/lib/pie.common'

    export default {
        name: "SexCountPie",
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
                    columns: ['性别', '人数'],
                    rows: (this.datas || [])
                }
            }
        },
        components: {VePie}
    }
</script>

<style scoped>
</style>
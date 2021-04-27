<template>
    <VeBar :data="chartData"></VeBar>
</template>

<script>
    import VeBar from 'v-charts/lib/bar.common'

    export default {
        name: "HourCountBar",
        data() {
            return {
                chartSettings: {
                    dimension: ['小时'],
                    metrics: ['回复数']
                }
            }
        },
        props: ['datas'],
        computed: {
            chartData() {
                return {
                    columns: ['小时', '回复数'],
                    rows: (this.datas || [])
                }
            }
        },
        components: {VeBar}
    }
</script>

<style scoped>

</style>
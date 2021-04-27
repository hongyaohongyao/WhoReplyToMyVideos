<template>
    <div>
        <Header :processing_info="process" :uid="uid"
                @waitingData="waitingData"></Header>
        <el-row>
            <el-col :span="12" align="center">
                <el-card class="box-card">
                    <div slot="header">
                        <span>统计信息: 用户性别/等级比例</span>
                    </div>
                    <el-row class="statistic-info-class">
                        <el-col :span="12" align="left">
                            <SexCountPie :datas="sexCount"></SexCountPie>
                        </el-col>
                        <el-col :span="12" align="left">
                            <LevelCountPie :datas="levelCount"></LevelCountPie>
                        </el-col>
                    </el-row>
                </el-card>
            </el-col>
            <el-col :span="12" align="center">
                <el-card class="box-card">
                    <div slot="header">
                        <span>统计信息: 用户回复时间</span>
                    </div>
                    <HourCountBar :datas="hourCount"></HourCountBar>
                </el-card>
            </el-col>
        </el-row>
        <el-container>
            <el-row>
                <el-col :span="12" align="center">
                    <ActiveUserTable title="评论次数最多" :datas="rankRData"></ActiveUserTable>
                </el-col>
                <el-col :span="12" align="center">
                    <ActiveUserTable title="评论视频最多" :datas="rankVData"></ActiveUserTable>
                </el-col>
                <el-col :span="12" align="center">
                    <ActiveUserTable title="新的访客" :datas="rankFData"></ActiveUserTable>
                </el-col>
                <el-col :span="12" align="center">
                    <ActiveUserTable title="最近评论" :datas="rankLData"></ActiveUserTable>
                </el-col>
            </el-row>
        </el-container>


    </div>
</template>

<script>
    import ElementUI from "element-ui";
    import SexCountPie from "../components/SexCountPie";
    import LevelCountPie from "../components/LevelCountPie";
    import HourCountBar from "../components/HourCountBar";
    import ActiveUserTable from "../components/ActiveUserTable";
    import Utils from '../js/utils'
    import Header from "../components/Header";

    export default {
        name: "Board",
        data() {
            return {
                uid: 0,
                process: '',
                sexCount: null,
                levelCount: null,
                hourCount: null,
                rankRData: null,
                rankVData: null,
                rankFData: null,
                rankLData: null,
                utils: Utils
            }
        },
        methods: {
            init() {
                const _this = this
                this.$axios.get("api/user/init", {params: {uid: this.uid}}).then(response => {
                    let res = response.data
                    if (res.code == 202) {
                        ElementUI.Message.info(res.msg)
                        _this.waitingData()
                    } else {
                        ElementUI.Message.error("unknown error")
                    }
                }).catch(err => {
                    ElementUI.Message.warning(JSON.stringify(err))
                })
            },
            waitingData() {
                const _this = this
                this.$axios.get("api/user/process", {params: {uid: this.uid}}).then(response => {
                    let res = response.data
                    _this.process = res.msg
                    if (res.code == 202) { // 数据还在分析当中
                        setTimeout(() => {
                            _this.waitingData()
                        }, 1500)
                    } else if (res.code == 200) { //数据已经分析完成
                        _this.setData()
                    } else {
                        ElementUI.Message.error("unknown error")
                    }
                }).catch(err => {
                    ElementUI.Message.warning(JSON.stringify(err))
                })
            },
            setData() {
                const _this = this
                this.$axios.get("api/statistics_info/sex", {params: {uid: this.uid}}).then(response => {
                    let res = response.data
                    _this.process = res.msg
                    if (res.code == 202) {
                        ElementUI.Message.warning("analysis processing")
                    } else if (res.code == 200) {
                        _this.setSexCount(res.data)
                    } else {
                        ElementUI.Message.error("unknown error")
                    }
                }).catch(err => {
                    ElementUI.Message.warning(JSON.stringify(err))
                })
                this.$axios.get("api/statistics_info/level", {params: {uid: this.uid}}).then(response => {
                    let res = response.data
                    _this.process = res.msg
                    if (res.code == 202) {
                        ElementUI.Message.warning("analysis processing")
                    } else if (res.code == 200) {
                        _this.setLevelCount(res.data)
                    } else {
                        ElementUI.Message.error("unknown error")
                    }
                }).catch(err => {
                    ElementUI.Message.warning(JSON.stringify(err))
                })
                this.$axios.get("api/statistics_info/hour", {params: {uid: this.uid}}).then(response => {
                    let res = response.data
                    _this.process = res.msg
                    if (res.code == 202) {
                        ElementUI.Message.warning("analysis processing")
                    } else if (res.code == 200) {
                        _this.setHourCount(res.data)
                    } else {
                        ElementUI.Message.error("unknown error")
                    }
                }).catch(err => {
                    ElementUI.Message.warning(JSON.stringify(err))
                })
                // this["rankRData"] = []
                this.setUserRank('replies',
                    'rankR',
                    'rankRData',
                    'timesR',
                    (v) => v + '次')
                this.setUserRank('videos',
                    'rankV',
                    'rankVData',
                    'timesV',
                    (v) => v + '个')
                this.setUserRank('new',
                    'rankF',
                    'rankFData',
                    'firstTime',
                    (v) => this.utils.FormatDateTime(v))
                this.setUserRank('last',
                    'rankL',
                    'rankLData',
                    'lastTime',
                    (v) => this.utils.FormatDateTime(v))

            },
            setSexCount(datas) {
                const _this = this
                this.sexCount = []
                datas.forEach((obj) => _this.sexCount.push({'性别': obj.label, '人数': obj.value}));
                this.sexCount = this.sexCount
            },
            setLevelCount(datas) {
                const _this = this
                this.levelCount = []
                datas.forEach((obj) => _this.levelCount.push({'等级': obj.label, '人数': obj.value}));
                this.levelCount = this.levelCount
            },
            setHourCount(datas) {
                const _this = this
                this.hourCount = []
                datas.forEach((obj) => _this.hourCount.push({'小时': parseInt(obj.label), '回复数': obj.value}));
                this.hourCount.sort((a, b) => a['小时'] - b['小时'])
                this.hourCount = this.hourCount
            },
            setUserRank(url, rankBy, datasName, valueName, func) {
                const _this = this
                this.$axios.get("api/active_user/" + url, {params: {uid: this.uid}}).then(response => {
                    let res = response.data
                    _this.process = res.msg
                    if (res.code == 202) {
                        ElementUI.Message.warning("analysis processing")
                    } else if (res.code == 200) {
                        _this.setUserRank0(res.data, rankBy, datasName, valueName, func)
                    } else {
                        ElementUI.Message.error("unknown error")
                    }
                }).catch(err => {
                    ElementUI.Message.warning(JSON.stringify(err))
                })
            },
            setUserRank0(datas, rankBy, datasName, valueName, func) {
                this[datasName] = []
                const _this = this
                datas.forEach((obj) => {
                    obj['rank'] = obj[rankBy]
                    obj['value'] = func(obj[valueName])
                    _this[datasName].push(obj)
                })
                this[datasName].sort((a, b) => a.rank - b.rank)
                this[datasName] = this[datasName]
            }
        },
        created() {
            this.uid = parseInt(this.$route.params.uid)
            this.init()
        },
        components: {SexCountPie, LevelCountPie, HourCountBar, ActiveUserTable, Header}
    }
</script>

<style scoped>
    .box-card {
        width: 98%;
    }
</style>
<template>
    <div>
        <head>
            <meta name="referrer" content="no-referrer">
        </head>
        <el-card class="box-card">
            <div slot="header" class="clearfix">
                <span>{{title}}</span>
                <el-button style="float: right; padding: 3px 0" type="text"
                           @click="moreUsers()">more
                </el-button>
            </div>
            <div v-for="o in datasFinal.slice(0,5)" :key="o.mid" class="text item">
                <el-row>
                    <el-col :span="5">
                        {{o.rank}}
                    </el-col>
                    <el-col :span="5">
                        <a :href="'https://space.bilibili.com/' + o.mid"
                           :target="'user_space'+ o.mid">
                            <el-avatar shape="square" :size="50" :src="o.face"/>
                        </a>
                    </el-col>
                    <el-col :span="6">
                        {{o.name}}
                    </el-col>
                    <el-col :span="6">
                        {{o.value}}
                    </el-col>
                </el-row>
            </div>
        </el-card>

        <el-dialog :title="title" :visible.sync="visibleSync" width="auto">
            <el-table style="width: 100%;"
                      max-height="600"
                      :data="dataPage">
                <el-table-column label="排名" prop="rank" width="50"></el-table-column>
                <el-table-column label="指标" prop="value"></el-table-column>
                <el-table-column label="头像">
                    <template slot-scope="scope">
                        <a :href="'https://space.bilibili.com/' + scope.row.mid"
                           :target="'user_space'+ scope.row.mid">
                            <el-avatar shape="square" :size="50" :src="scope.row.face"/>
                        </a>
                    </template>
                </el-table-column>
                <el-table-column label="用户名称" prop="name"></el-table-column>
                <el-table-column label="用户id" prop="mid"></el-table-column>
                <el-table-column label="性别" prop="sex"></el-table-column>
                <el-table-column label="等级" prop="level"></el-table-column>
                <el-table-column label="首次访问">
                    <template slot-scope="scope">
                        {{utils.FormatDateTime(scope.row.firstTime)}}
                    </template>
                </el-table-column>
                <el-table-column label="最后访问">
                    <template slot-scope="scope">
                        {{utils.FormatDateTime(scope.row.lastTime)}}
                    </template>
                </el-table-column>
            </el-table>
            <el-pagination
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
                    :current-page="currentPage"
                    :page-sizes="[5, 10, 20, 40]"
                    :page-size="pageSize"
                    layout="total, sizes, prev, pager, next, jumper"
                    :total="datasFinal.length||0">
            </el-pagination>
        </el-dialog>
    </div>
</template>

<script>
    import Utils from '../js/utils'

    export default {
        name: "ActiveUserTable",
        data() {
            return {
                currentPage: 1,
                pageSize: 10,
                visibleSync: false,
                utils: Utils
            }
        },
        props: ["title", "datas"],
        methods: {
            handleSizeChange: function (size) {
                this.pageSize = size;
            },
            handleCurrentChange: function (currentPage) {
                this.currentPage = currentPage;
            },
            moreUsers() {
                this.visibleSync = true;
            }
        },
        computed: {
            datasFinal() {
                return this.datas || []
            },
            dataPage() {
                return this.datasFinal.slice((this.currentPage - 1) * this.pageSize, this.currentPage * this.pageSize)
            }
        }
    }
</script>

<style scoped>
    .box-card {
        width: 600px;
    }
</style>
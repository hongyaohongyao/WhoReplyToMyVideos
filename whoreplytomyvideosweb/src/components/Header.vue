<template>
    <div>
        <el-header height="60px">
            <el-dropdown @command="handleCommand">
                <i class="el-icon-setting"
                   type="primary"
                   style="color:lightgreen;margin-right: 20px"></i>
                <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item :command="1">更新</el-dropdown-item>
                    <el-dropdown-item :command="2">返回</el-dropdown-item>
                </el-dropdown-menu>
            </el-dropdown>
            <span>{{uid}}:{{processing_info}}</span>
        </el-header>
    </div>
</template>

<script>
    import ElementUI from "element-ui";

    export default {
        name: "Head",
        props: ['uid', 'processing_info'],
        methods: {
            handleCommand(command) {
                switch (command) {
                    case 1:
                        this.update()
                        break;
                    case 2:
                        this.back()
                        break;
                }
            },
            back() {
                this.$router.push('/Index')
            },
            update() {
                const _this = this
                this.$axios.get("api/user/update", {params: {uid: this.uid}}).then(response => {
                    let res = response.data
                    if (res.code == 200) {
                        ElementUI.Message.info('updating user')
                        _this.$emit('waitingData')
                    } else if (res.code == 202) {
                        ElementUI.Message.info(res.msg)
                    } else if (res.code == 404) {
                        ElementUI.Message.warning("unknown user")
                    } else {
                        ElementUI.Message.error("unknown error")
                    }
                }).catch(err => {
                    ElementUI.Message.warning(JSON.stringify(err))
                })
            }
        }
    }
</script>

<style scoped>
    .el-header {
        background-color: #2F4F4F;
        color: #FFFFFF;
        text-align: left;
        line-height: 55px;
        font-size: 20px;
    }
</style>
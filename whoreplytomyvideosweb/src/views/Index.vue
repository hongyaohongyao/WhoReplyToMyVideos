<template>
    <div style="height: 100%">
        <el-dialog title="Who you are?" center :visible="true">
            <el-container style="height: 100%">
                <el-main style="height: 100%">
                    <el-form ref="form" :model="form" label-width="150px">
                        <el-form-item label="My bilibili uid">
                            <el-input v-model="form.uid"></el-input>
                        </el-form-item>
                        <el-button type="primary" @click="onSubmit">I'm sure</el-button>
                    </el-form>
                </el-main>
            </el-container>
        </el-dialog>
    </div>
</template>

<script>

    export default {
        name: "Index",
        data() {
            return {
                form: {
                    uid: ''
                }
            }
        },
        methods: {
            onSubmit() {
                const uid = this.form.uid;
                this.$router.push('/' + uid)
            }
        }
    }
</script>

<style scoped>
    .el-main {
        color: #333;
        text-align: center;
        line-height: 100px;
        height: 100%;
    }

    .el-container {
        margin-bottom: 40px;
    }

    .el-form {
        max-width: 500px;
        margin: 0 auto;
    }
</style>
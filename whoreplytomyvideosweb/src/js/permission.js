import router from "../router";
import ElementUI from 'element-ui'

router.beforeEach((to, from, next) => {
    if (to.meta.hasUid) {
        const uid = parseInt(to.params.uid)
        if (isNaN(uid) || uid < 1 || uid > 700000000) {
            ElementUI.Message.warning("无效的uid")
            next('/Index')
        }
    }
    next()
})
import Vue from 'vue'
import VueRouter from 'vue-router'
import Index from "../views/Index";
import Board from "../views/Board";

Vue.use(VueRouter)

const routes = [
    {
        path: '/',
        name: 'Root',
        redirect: {name: 'Index'}
    },
    {
        path: '/Index',
        name: 'Index',
        component: Index
    },
    {
        path: '/:uid',
        name: 'Board',
        component: ()=>import('../views/Board'),
        meta: {
            hasUid: true
        }
    }
]

const router = new VueRouter({
    mode: 'history',
    base: process.env.BASE_URL,
    routes
})

const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location) {
    return originalPush.call(this, location).catch(err => err)
}

export default router

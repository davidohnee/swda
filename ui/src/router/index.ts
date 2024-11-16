import { createRouter, createWebHistory } from "vue-router";

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: "/",
            name: "home",
            redirect: { name: "inventory" },
        },
        {
            path: "/inventory",
            name: "inventory",
            component: () => import("../views/InventoryView.vue"),
        },
        {
            path: "/inventory/:id",
            name: "product",
            component: () => import("../views/InventoryItemView.vue"),
        },
        {
            path: "/customers",
            name: "customers",
            component: () => import("../views/CustomersView.vue"),
        },
        {
            path: "/orders",
            name: "orders",
            component: () => import("../views/OrdersView.vue"),
        },
        {
            path: "/orders/:id",
            name: "order",
            component: () => import("../views/OrderView.vue"),
        },
        {
            path: "/replenishments",
            name: "replenishments",
            component: () => import("../views/ReplenishmentsView.vue"),
        },
    ],
});

export default router;

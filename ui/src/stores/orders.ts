import { ref } from "vue";
import { defineStore } from "pinia";
import type { MicronautErrorResponse, Order } from "@/types";
import { type ApiResponse } from "@/api/helper";
import { api } from "@/api";

export const useOrderStore = defineStore("orders", () => {
    const response = ref<ApiResponse<Order[]>>();
    const orders = ref<Order[]>([]);
    const error = ref<MicronautErrorResponse | null>(null);

    const fetchOrders = async () => {
        response.value = await api.orders.getAll();

        if (response.value.error) {
            error.value = response.value.error;
        } else {
            orders.value = response.value.data;
        }
    };
    fetchOrders();

    return {
        response,
        orders,
        error,
        getOrderById: (id: string) => orders.value.find((o) => o.id === id),
        refresh: fetchOrders,
    };
});

import { ref } from "vue";
import { defineStore } from "pinia";
import type { InventoryItem, MicronautErrorResponse, Order } from "@/types";
import { type ApiResponse } from "@/api/helper";
import { api } from "@/api";

export const useInventoryStore = defineStore("inventory", () => {
    const response = ref<ApiResponse<InventoryItem[]>>();
    const inventory = ref<InventoryItem[]>([]);
    const error = ref<MicronautErrorResponse | null>(null);

    const fetchInventory = async () => {
        response.value = await api.inventory.getAll();

        if (response.value.error) {
            error.value = response.value.error;
        } else {
            inventory.value = response.value.data;
        }
    };
    fetchInventory();

    return {
        response,
        inventory,
        error,
        getProductById: (id: number) =>
            inventory.value.find((o) => o.product.id === id),
        refresh: fetchInventory,
    };
});

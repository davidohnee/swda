import { ref } from "vue";
import { defineStore } from "pinia";
import type { MicronautErrorResponse, ReplenishmentItem } from "@/types";
import { type ApiResponse } from "@/api/helper";
import { api } from "@/api";

export const useReplenishmentStore = defineStore("replenishments", () => {
    const response = ref<ApiResponse<ReplenishmentItem[]>>();
    const replenishments = ref<ReplenishmentItem[]>([]);
    const error = ref<MicronautErrorResponse | null>(null);

    const fetchReplenishments = async () => {
        response.value = await api.replenishments.getAll();

        if (response.value.error) {
            error.value = response.value.error;
        } else {
            replenishments.value = response.value.data;
        }
    };
    fetchReplenishments();

    return {
        response,
        replenishments,
        error,
        getForProduct: (productId: number) =>
            replenishments.value.filter((r) => r.product.id === productId),
        refresh: fetchReplenishments,
    };
});

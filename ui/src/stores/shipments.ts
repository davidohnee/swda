import { ref } from "vue";
import { defineStore } from "pinia";
import type { MicronautErrorResponse, Shipment } from "@/types";
import { type ApiResponse } from "@/api/helper";
import { api } from "@/api";

export const useShipmentStore = defineStore("shipments", () => {
    const response = ref<ApiResponse<Shipment[]>>();
    const shipments = ref<Shipment[]>([]);
    const error = ref<MicronautErrorResponse | null>(null);

    const fetchShipments = async () => {
        response.value = await api.shipments.getAll();

        if (response.value.error) {
            error.value = response.value.error;
        } else {
            shipments.value = response.value.data;
        }
    };
    fetchShipments();

    return {
        response,
        shipments,
        error,
        getByOrderId: (orderId: string) =>
            shipments.value.find((o) => o.orderId == orderId),
        refresh: fetchShipments,
    };
});

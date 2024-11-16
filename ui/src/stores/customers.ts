import { ref } from "vue";
import { defineStore } from "pinia";
import type {
    Customer,
    InventoryItem,
    MicronautErrorResponse,
    Order,
} from "@/types";
import { type ApiResponse } from "@/api/helper";
import { api } from "@/api";

export const useCustomerStore = defineStore("customers", () => {
    const response = ref<ApiResponse<Customer[]>>();
    const customers = ref<Customer[]>([]);
    const error = ref<MicronautErrorResponse | null>(null);

    const fetchCustomers = async () => {
        response.value = await api.customers.getAll();

        if (response.value.error) {
            error.value = response.value.error;
        } else {
            customers.value = response.value.data;
        }
    };
    fetchCustomers();

    return {
        response,
        customers,
        error,
        refresh: fetchCustomers,
    };
});

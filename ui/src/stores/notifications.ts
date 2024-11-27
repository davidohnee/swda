import { ref } from "vue";
import { defineStore } from "pinia";
import type { MicronautErrorResponse, Notification } from "@/types";
import { type ApiResponse } from "@/api/helper";
import { api } from "@/api";

export const useNotificationStore = defineStore("notifications", () => {
    const response = ref<ApiResponse<Notification[]>>();
    const notifications = ref<Notification[]>([]);
    const error = ref<MicronautErrorResponse | null>(null);

    const fetchNotifications = async () => {
        response.value = await api.notifications.getAll();

        if (response.value.error) {
            error.value = response.value.error;
        } else {
            notifications.value = response.value.data;
        }
    };
    fetchNotifications();

    return {
        response,
        notifications,
        error,
        refresh: fetchNotifications,
    };
});

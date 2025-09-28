import type { Notification } from "@/types";
import { mapApiResponse, type ApiResponse } from "./helper";

export const notifications = {
    _mapNotificationItem: (item: any): Notification => ({
        ...item,
        sent: item.sent ? new Date(item.sent) : null,
    }),
    async getAll(): Promise<ApiResponse<Notification[]>> {
        return fetch("/api/v1/notifications")
            .then(mapApiResponse<any[]>)
            .then((items) => {
                if (items.error) {
                    return items;
                }
                return {
                    ...items,
                    data: items.data.map(this._mapNotificationItem),
                };
            });
    },
    async get(id: string): Promise<ApiResponse<Notification>> {
        return fetch(`/api/v1/notifications/${id}`)
            .then(mapApiResponse<any>)
            .then((item) => {
                if (item.error) {
                    return item;
                }
                return {
                    ...item,
                    data: this._mapNotificationItem(item.data),
                };
            });
    },
};

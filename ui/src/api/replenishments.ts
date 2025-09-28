import type { ReplenishmentItem } from "@/types";
import { mapApiResponse, type ApiResponse } from "./helper";

export const replenishments = {
    _mapReplenishmentItem: (item: any): ReplenishmentItem => ({
        ...item,
        deliveryDate: item.deliveryDate ? new Date(item.deliveryDate) : null,
    }),
    async getAll(): Promise<ApiResponse<ReplenishmentItem[]>> {
        return fetch("/api/v1/replenishments")
            .then(mapApiResponse<any>)
            .then((items) => {
                if (items.error) {
                    return items;
                }
                return {
                    ...items,
                    data: items.data.map(replenishments._mapReplenishmentItem),
                };
            });
    },
    async get(id: string): Promise<ApiResponse<ReplenishmentItem>> {
        return fetch(`/api/v1/replenishments/${id}`)
            .then(mapApiResponse<any>)
            .then((item) => {
                if (item.error) {
                    return item;
                }
                return {
                    ...item,
                    data: replenishments._mapReplenishmentItem(item.data),
                };
            });
    },
};

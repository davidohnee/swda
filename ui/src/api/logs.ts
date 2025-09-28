import type { Log } from "@/types";
import { mapApiResponse, type ApiResponse } from "./helper";

export const logs = {
    _mapLogItem: (item: any): Log => ({
        ...item,
        timestamp: item.timestamp ? new Date(item.timestamp) : null,
    }),
    async getAll(): Promise<ApiResponse<Log[]>> {
        return fetch("/api/v1/logs")
            .then(mapApiResponse<any[]>)
            .then((items) => {
                if (items.error) {
                    return items;
                }
                return {
                    ...items,
                    data: items.data.map(this._mapLogItem),
                };
            });
    },
    async forOrder(orderId: string): Promise<ApiResponse<Log[]>> {
        return fetch(`/api/v1/logs?corrId=${orderId}`)
            .then(mapApiResponse<any[]>)
            .then((items) => {
                if (items.error) {
                    return items;
                }
                return {
                    ...items,
                    data: items.data.map(this._mapLogItem),
                };
            });
    },
    async get(id: string): Promise<ApiResponse<Log>> {
        return fetch(`/api/v1/logs/${id}`)
            .then(mapApiResponse<any>)
            .then((item) => {
                if (item.error) {
                    return item;
                }
                return {
                    ...item,
                    data: this._mapLogItem(item.data),
                };
            });
    },
};

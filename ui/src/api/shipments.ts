import type { Shipment } from "@/types";
import { mapApiResponse, type ApiResponse } from "./helper";

export const shipments = {
    _mapOrderItem: (item: any): Shipment => ({
        ...item,
        estimatedArrival: item.estimatedArrival
            ? new Date(item.estimatedArrival)
            : null,
        departure: item.departure ? new Date(item.departure) : null,
    }),
    async getAll(): Promise<ApiResponse<Shipment[]>> {
        return fetch("/api/v1/shipments")
            .then(mapApiResponse<any[]>)
            .then((items) => {
                if (items.error) {
                    return items;
                }
                return {
                    ...items,
                    data: items.data.map(this._mapOrderItem),
                };
            });
    },
    async get(id: string): Promise<ApiResponse<Shipment>> {
        return fetch(`/api/v1/shipments/${id}`)
            .then(mapApiResponse<any>)
            .then((item) => {
                if (item.error) {
                    return item;
                }
                return {
                    ...item,
                    data: this._mapOrderItem(item.data),
                };
            });
    },
    async create(shipment: { orderId: string; departure: string; estimatedArrival: string }): Promise<ApiResponse<Shipment>> {
        return fetch("/api/v1/shipments", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(shipment),
        }).then(mapApiResponse<any>);
    },
};

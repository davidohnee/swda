import type { Order, OrderCreate, OrderItem } from "@/types";
import { mapApiResponse, type ApiResponse } from "./helper";

export const orders = {
    _mapOrderItem: (item: any): Order => ({
        ...item,
        dateTime: item.dateTime ? new Date(item.dateTime) : null,
    }),
    async getAll(): Promise<ApiResponse<Order[]>> {
        return fetch("/api/v1/orders")
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
    async get(id: string): Promise<ApiResponse<Order>> {
        return fetch(`/api/v1/orders/${id}`)
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
    async cancel(orderId: string) {
        return fetch(`/api/v1/orders/${orderId}`, {
            method: "PATCH",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                status: "CANCELLED",
            }),
        });
    },
    async create(order: OrderCreate) {
        return fetch("/api/v1/orders", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                ...order,
                dateTime: order.dateTime?.toISOString(),
            }),
        }).then(mapApiResponse<any>);
    },
};

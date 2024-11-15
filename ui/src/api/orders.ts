import type { ErrorResponse, Order } from "@/types";

export const orders = {
    async getAll(): Promise<Order[] | ErrorResponse> {
        return fetch("/api/v1/orders").then((res) => res.json());
    },
    async get(id: string): Promise<Order | ErrorResponse> {
        return fetch(`/api/v1/orders/${id}`).then((res) => res.json());
    },
};

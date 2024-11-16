import type { Order } from "@/types";
import { mapApiResponse, type ApiResponse } from "./helper";

export const orders = {
    async getAll() {
        return fetch("/api/v1/orders").then(mapApiResponse<Order[]>);
    },
    async get(id: string) {
        return fetch(`/api/v1/orders/${id}`).then(mapApiResponse<Order>);
    },
};

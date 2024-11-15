import type { Customer, ErrorResponse } from "@/types";

export const cutomers = {
    async getAll(): Promise<Customer[] | ErrorResponse> {
        return fetch("/api/v1/customers").then((res) => res.json());
    },
    async get(id: string): Promise<Customer | ErrorResponse> {
        return fetch(`/api/v1/customers/${id}`).then((res) => res.json());
    },
};

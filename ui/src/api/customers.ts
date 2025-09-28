import type { Customer } from "@/types";
import { mapApiResponse } from "./helper";

export const customers = {
    async getAll() {
        return fetch("/api/v1/customers").then(mapApiResponse<Customer[]>);
    },
    async get(id: string) {
        return fetch(`/api/v1/customers/${id}`).then(mapApiResponse<Customer>);
    },
};

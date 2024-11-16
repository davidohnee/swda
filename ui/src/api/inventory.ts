import type { InventoryItem } from "@/types";
import { mapApiResponse } from "./helper";

export const inventory = {
    async getAll() {
        return fetch("/api/v1/inventory").then(mapApiResponse<InventoryItem[]>);
    },
    async get(id: number) {
        return fetch(`/api/v1/inventory/${id}`).then(
            mapApiResponse<InventoryItem>
        );
    },
    async update(
        id: number,
        item: {
            quantity: number;
            replenishmentThreshold?: number;
        }
    ) {
        return fetch(`/api/v1/inventory/${id}`, {
            method: "PATCH",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(item),
        }).then(mapApiResponse<any>);
    },
};

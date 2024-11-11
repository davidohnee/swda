import type { ReplenishmentItem } from "@/types";

export const replenishments = {
    async getAll(): Promise<ReplenishmentItem[]> {
        return fetch("/api/v1/replenishments").then((res) => res.json());
    },
    async get(id: string): Promise<ReplenishmentItem> {
        return fetch(`/api/v1/replenishments/${id}`).then((res) => res.json());
    },
};

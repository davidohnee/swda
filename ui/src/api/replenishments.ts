import type { ErrorResponse, ReplenishmentItem } from "@/types";

export const replenishments = {
    _mapReplenishmentItem: (item: any) => ({
        ...item,
        deliveryDate: item.deliveryDate ? new Date(item.deliveryDate) : null,
    }),
    async getAll(): Promise<ReplenishmentItem[] | ErrorResponse> {
        return fetch("/api/v1/replenishments")
            .then((res) => res.json())
            .then((items) => items.map(replenishments._mapReplenishmentItem));
    },
    async get(id: string): Promise<ReplenishmentItem | ErrorResponse> {
        return fetch(`/api/v1/replenishments/${id}`)
            .then((res) => res.json())
            .then(replenishments._mapReplenishmentItem);
    },
};

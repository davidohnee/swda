export interface Product {
    id: number;
    name: string;
    price: number;
}

export interface InventoryItem {
    product: Product;
    quantity: number;
    replenishmentThreshold: number;
}

export const api = {
    inventory: {
        async getAll(): Promise<InventoryItem[]> {
            return fetch("/api/v1/inventory").then((res) => res.json());
        },
        async get(id: number): Promise<InventoryItem> {
            return fetch(`/api/v1/inventory/${id}`).then((res) => res.json());
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
            }).then((res) => res.json());
        },
    },
};

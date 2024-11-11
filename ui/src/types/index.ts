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

export interface ReplenishmentItem {
    trackingId: string;
    product: Product;
    quantity: number;
    status: "PENDING" | "CONFIRMED" | "DONE";
}

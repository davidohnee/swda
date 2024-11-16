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
    deliveryDate: Date | null;
}

interface Person {
    id: string;
    firstName: string;
    familyName: string;
}

export interface Customer extends Person {
    address: {
        streetName: string;
        streetNumber: string;
        townName: string;
        plz: string;
    };
    contactInfo: {
        email: string;
        phone: string;
    };
}

export interface Employee extends Person {
    role: "MANAGER" | "SALES";
}

export interface OrderItem {
    product: Product;
    quantity: number;
}

export interface Order {
    id: string;
    dateTime: Date;
    status: "PENDING" | "CONFIRMED" | "DONE";
    orderItems: OrderItem[];
    price: number;
    orderType: "CUSTOMER_ORDER" | "REPLENISHMENT_ORDER";
    customer: Customer;
    seller: Employee;
}

export interface MicronautErrorResponse {
    message: string;
    _links: {
        self: {
            href: string;
        }[];
    };
    _embedded: {
        errors: {
            message: string;
        }[];
    };
}

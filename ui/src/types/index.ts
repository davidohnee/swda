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

export interface OrderCreate {
    dateTime: Date;
    orderItems: {
        productId: number;
        quantity: number;
    }[];
    orderType: "CUSTOMER_ORDER" | "REPLENISHMENT_ORDER";
    customerId: string;
    sellerId: string;
    destinationId: string;
}

export interface Order {
    id: string;
    status: "PENDING" | "CONFIRMED" | "DONE";
    dateTime: Date;
    orderItems: OrderItem[];
    price: number;
    orderType: "CUSTOMER_ORDER" | "REPLENISHMENT_ORDER";
    customer: Customer;
    seller: Employee;
}

export interface Shipment {
    id: string;
    orderId: string;
    order: Order;
    departure: Date;
    estimatedArrival: Date;
}

export interface ShipmentCreate {
    orderId: string;
    departure: string;
    estimatedArrival: string;
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

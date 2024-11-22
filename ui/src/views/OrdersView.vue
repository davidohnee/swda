<script setup lang="ts">
    import CreateOrderDialog from "@/components/CreateOrderDialog.vue";
    import CreateShipmentDialog from "@/components/CreateShipmentDialog.vue";
    import ErrorLoader from "@/components/ErrorLoader.vue";
    import { useOrderStore } from "@/stores/orders";
    import { ref } from "vue";

    const orders = useOrderStore();

    const orderDialog = ref<typeof CreateOrderDialog>();
    const shipmentDialog = ref<typeof CreateShipmentDialog>();

    const order = () => {
        orderDialog.value!.open();
    };

    const createShipment = (orderId: string) => {
        shipmentDialog.value!.open(orderId);
    };
</script>
<template>
    <div class="orders">
        <h1>Orders</h1>
        <button
            class="create"
            @click="order"
        >
            Create order
        </button>
        <ErrorLoader :content="orders.response">
            <div>
                <CreateOrderDialog
                    @close="orders.refresh"
                    ref="orderDialog"
                />
                <CreateShipmentDialog
                    @close="orders.refresh"
                    ref="shipmentDialog"
                />
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Items</th>
                            <th>Total Price</th>
                            <th>Customer</th>
                            <th>Employee</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr
                            v-for="item in orders.orders"
                            :key="item.id"
                        >
                            <td>
                                <RouterLink :to="`/orders/${item.id}`">
                                    {{ item.id }}
                                </RouterLink>
                            </td>
                            <td>
                                <ul>
                                    <li
                                        v-for="orderItem in item.orderItems"
                                        :key="orderItem.product.id"
                                    >
                                        {{ orderItem.product.name }} x{{
                                            orderItem.quantity
                                        }}
                                    </li>
                                </ul>
                            </td>
                            <td>{{ item.price }}</td>
                            <td>
                                {{
                                    item.customer.firstName +
                                    " " +
                                    item.customer.familyName
                                }}
                            </td>
                            <td>
                                {{
                                    item.seller.firstName +
                                    " " +
                                    item.seller.familyName
                                }}
                            </td>
                            <td>
                                <div
                                    class="status"
                                    :class="{
                                        success: ['DONE', 'CONFIRMED'].includes(
                                            item.status
                                        ),
                                        info: item.status === 'PENDING',
                                    }"
                                >
                                    {{ item.status }}
                                </div>
                            </td>
                            <td>
                                <button @click="createShipment(item.id)">Create Shipment</button>
                            </td>
                        </tr>
                    </tbody>
                </table>
                <p
                    class="muted center"
                    v-if="orders.orders.length === 0"
                >
                    <em> No orders found. </em>
                </p>
            </div>
        </ErrorLoader>
    </div>
</template>

<style scoped>
    .orders {
        display: flex;
        flex-direction: column;
    }

    .create {
        margin-bottom: 1rem;
        margin-left: auto;
    }
</style>

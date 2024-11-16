<script setup lang="ts">
    import { ref, onMounted, computed } from "vue";
    import { api } from "@/api";
    import type { Order } from "@/types";
    import ErrorLoader from "@/components/ErrorLoader.vue";
    import type { ApiResponse } from "@/api/helper";

    const response = ref<ApiResponse<Order[]>>();
    const orders = computed(() => (response.value?.data ?? []) as Order[]);

    const fetchOrders = async () => {
        response.value = await api.orders.getAll();
    };

    onMounted(() => {
        fetchOrders();
    });
</script>
<template>
    <div class="orders">
        <h1>Orders</h1>
        <ErrorLoader :content="response">
            <div>
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Items</th>
                            <th>Total Price</th>
                            <th>Customer</th>
                            <th>Employee</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr
                            v-for="item in orders"
                            :key="item.id"
                        >
                            <td>{{ item.id }}</td>
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
                        </tr>
                    </tbody>
                </table>
                <p
                    class="muted center"
                    v-if="orders.length === 0"
                >
                    <em> No orders found. </em>
                </p>
            </div>
        </ErrorLoader>
    </div>
</template>

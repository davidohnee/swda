<script setup lang="ts">
    import { api } from "@/api";
    import ErrorLoader from "@/components/ErrorLoader.vue";
    import LogComponent from "@/components/Log.vue";
    import { useOrderStore } from "@/stores/orders";
    import type { Log, Order } from "@/types";
    import { computed, onMounted, ref, watch } from "vue";
    import { useRoute } from "vue-router";

    const orders = useOrderStore();
    const order = ref<Order>();
    const route = useRoute();
    const orderId = computed(() => {
        return String(route.params.id);
    });
    const logs = ref<Log[]>();

    const updateOrder = async () => {
        console.log("Updating order", orderId.value);
        order.value = orders.getOrderById(orderId.value);
        logs.value = (await api.logs.forOrder(orderId.value)).data ?? [];
        console.log("Order", order.value);
    };

    const cancelOrder = async () => {
        await api.orders.cancel(orderId.value);
        setTimeout(updateOrder, 500);
    };

    onMounted(updateOrder);
    watch(orderId, updateOrder);
    watch(() => orders.orders, updateOrder);
</script>
<template>
    <div class="orders">
        <div class="title">
            <h2>Order ID: {{ orderId }}</h2>
            <div
                v-if="order"
                class="status"
                :class="{
                    success: ['DONE', 'CONFIRMED'].includes(order.status),
                    info: order.status === 'PENDING',
                    disabled: ['CANCELLED'].includes(order.status),
                }"
            >
                {{ order.status }}
            </div>
        </div>
        <ErrorLoader :content="orders.response">
            <span class="muted">
                {{ order?.dateTime.toLocaleString() }}
            </span>
            <div
                class="distribute"
                v-if="order"
            >
                <main>
                    <div class="card">
                        <h3>Items</h3>
                        <div class="items">
                            <div
                                v-for="item in order.orderItems"
                                class="item"
                            >
                                <RouterLink
                                    :to="`/inventory/${item.product.id}`"
                                >
                                    <strong>
                                        {{ item.product.name || "N/A" }}
                                    </strong>
                                </RouterLink>
                                <div class="quantity-and-price">
                                    <span>{{ item.quantity }}</span>
                                    x
                                    <span>${{ item.product.price }}</span>
                                </div>
                                <div class="total">
                                    <span>
                                        ${{
                                            item.quantity * item.product.price
                                        }}
                                    </span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="card">
                        <h3>Order summary</h3>
                        <div class="item">
                            <strong>Total</strong>
                            <strong>${{ order.price }}</strong>
                        </div>
                        <div class="item">
                            <span>Paid by customer</span>
                            <span>$0.00</span>
                        </div>
                        <button
                            @click="cancelOrder"
                            v-if="
                                ['PENDING', 'CONFIRMED'].includes(order.status)
                            "
                            class="error cancel-order"
                        >
                            CANCEL
                        </button>
                    </div>
                    <div class="card">
                        <h3>Timeline</h3>
                        <LogComponent
                            v-for="log in logs"
                            :log="log"
                        />
                    </div>
                </main>
                <aside>
                    <div class="card">
                        <h3>Customer</h3>
                        <div class="icon-text-list">
                            <div class="icon-text">
                                <span class="material-symbols-rounded">
                                    person
                                </span>
                                <span>
                                    {{
                                        order.customer.firstName +
                                        " " +
                                        order.customer.familyName
                                    }}
                                </span>
                            </div>
                            <div
                                class="icon-text"
                                v-if="order.customer.contactInfo.email"
                            >
                                <span class="material-symbols-rounded">
                                    email
                                </span>
                                <span>{{
                                    order.customer.contactInfo.email
                                }}</span>
                            </div>
                            <div
                                class="icon-text"
                                v-if="order.customer.contactInfo.phone"
                            >
                                <span class="material-symbols-rounded">
                                    phone
                                </span>
                                <span>{{
                                    order.customer.contactInfo.phone
                                }}</span>
                            </div>
                        </div>
                    </div>
                    <div class="card">
                        <h3>Seller</h3>
                        <div class="icon-text-list">
                            <div class="icon-text">
                                <span class="material-symbols-rounded">
                                    person
                                </span>
                                <span>
                                    {{
                                        order.seller.firstName +
                                        " " +
                                        order.seller.familyName
                                    }}
                                </span>
                            </div>
                        </div>
                    </div>
                    <div class="card">
                        <h3>Shipping Address</h3>
                        <div class="icon-text-list">
                            <div class="icon-text">
                                <span class="material-symbols-rounded">
                                    location_on
                                </span>
                                <span>
                                    {{ order.customer.address.streetNumber }}
                                    {{ order.customer.address.streetName }}
                                </span>
                            </div>
                            <div>
                                <span>
                                    {{ order.customer.address.townName }}
                                    {{ order.customer.address.plz }}
                                </span>
                            </div>
                        </div>
                    </div>
                </aside>
            </div>
        </ErrorLoader>
    </div>
</template>

<style scoped>
    .title {
        display: flex;
        align-items: center;
        gap: 1em;

        .status {
            font-size: 0.8rem;
        }
    }

    .card {
        padding: 1em;

        & h3 {
            margin: 0;
        }

        .cancel-order {
            width: 100%;
        }
    }

    .distribute {
        display: flex;
        flex-direction: row;
        flex-wrap: wrap;
        gap: 1em;
        margin-top: 1em;
    }

    main {
        flex: 1;
        display: grid;
        gap: 1em;
    }

    aside {
        flex: 1;
        max-width: 30%;
        min-width: 50ch;
    }

    .item {
        display: grid;
        grid-template-columns: 1fr 10ch 5ch;
        gap: 1em;
        align-items: center;
        padding: 1em;

        & .quantity-and-price {
            display: flex;
            gap: 0.5em;
            border: 1px solid var(--border);
            border-radius: 0.5em;
            justify-content: center;
            padding: 0.5em;
        }
    }

    .icon-text-list {
        display: grid;
        gap: 1em;

        & .icon-text {
            display: flex;
            gap: 0.5em;
            align-items: center;
        }

        & span.material-symbols-rounded {
            font-size: 1.3rem;
        }
    }
</style>

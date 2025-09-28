<script setup lang="ts">
    import ErrorLoader from "@/components/ErrorLoader.vue";
    import { useShipmentStore } from "@/stores/shipments";

    const shipments = useShipmentStore();
</script>
<template>
    <div class="shipments">
        <h1>Shipments</h1>
        <ErrorLoader :content="shipments.response">
            <div>
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Order</th>
                            <th>Departure</th>
                            <th>Estimated Arrival</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr
                            v-for="item in shipments.shipments"
                            :key="item.id"
                        >
                            <td>
                                {{ item.id }}
                            </td>
                            <td>
                                <RouterLink :to="`/orders/${item.orderId}`">
                                    {{ item.orderId }}
                                </RouterLink>
                            </td>
                            <td>
                                {{ item.departure.toLocaleString() }}
                            </td>
                            <td>
                                {{ item.estimatedArrival.toLocaleString() }}
                            </td>
                        </tr>
                    </tbody>
                </table>
                <p
                    class="muted center"
                    v-if="shipments.shipments.length === 0"
                >
                    <em> No shipments found. </em>
                </p>
            </div>
        </ErrorLoader>
    </div>
</template>

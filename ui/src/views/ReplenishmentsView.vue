<script setup lang="ts">
    import ErrorLoader from "@/components/ErrorLoader.vue";
    import { useReplenishmentStore } from "@/stores/replenishments";

    const replenishments = useReplenishmentStore();
</script>
<template>
    <div class="replenishments">
        <h1>Replenishments</h1>
        <ErrorLoader :content="replenishments.response">
            <div>
                <table>
                    <thead>
                        <tr>
                            <th>Tracking ID</th>
                            <th>Product Name</th>
                            <th>Quantity</th>
                            <th>Delivery Date</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr
                            v-for="item in replenishments.replenishments"
                            :key="item.trackingId"
                        >
                            <td>{{ item.trackingId }}</td>
                            <td>
                                <RouterLink
                                    :to="`/inventory/${item.product.id}`"
                                >
                                    {{ item.product.name }}
                                </RouterLink>
                            </td>
                            <td>{{ item.quantity }}</td>
                            <td>
                                {{
                                    item.deliveryDate
                                        ? item.deliveryDate.toLocaleDateString()
                                        : "N/A"
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
                    v-if="replenishments.replenishments.length === 0"
                >
                    <em> No replenishments found. </em>
                </p>
            </div>
        </ErrorLoader>
    </div>
</template>

<script setup lang="ts">
    import { ref, onMounted, computed } from "vue";
    import { api } from "@/api";
    import type { ErrorResponse, ReplenishmentItem } from "@/types";
    import ErrorLoader from "@/components/ErrorLoader.vue";

    const response = ref<ReplenishmentItem[] | ErrorResponse>();
    const replenishments = computed(
        () => response.value as ReplenishmentItem[]
    );

    const fetchReplenishments = async () => {
        response.value = await api.replenishments.getAll();
    };

    onMounted(() => {
        fetchReplenishments();
    });
</script>
<template>
    <div class="replenishments">
        <h1>Replenishments</h1>
        <ErrorLoader :content="response">
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
                            v-for="item in replenishments"
                            :key="item.trackingId"
                        >
                            <td>{{ item.trackingId }}</td>
                            <td>{{ item.product.name }}</td>
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
                    v-if="replenishments.length === 0"
                >
                    <em> No replenishments found. </em>
                </p>
            </div>
        </ErrorLoader>
    </div>
</template>

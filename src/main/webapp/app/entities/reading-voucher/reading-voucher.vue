<template>
  <div>
    <h2 id="reading-voucher-heading">Reading Vouchers</h2>
    <b-form @submit.prevent="upload">
      <b-form-file v-model="file" required></b-form-file>
      <b-button type="submit" variant="primary" class="mt-2">Upload</b-button>
    </b-form>
    <b-button class="mt-3" variant="success" @click="claimVoucher">Claim Voucher</b-button>
    <div v-if="claimedCode" class="alert alert-info mt-3">Claimed voucher: {{ claimedCode }}</div>
  </div>
</template>

<script lang="ts" src="./reading-voucher.component.ts"></script>

<template>
  <div>
    <b-dropdown-item to="/novel">
      <font-awesome-icon icon="asterisk" />
      <span>Novel</span>
    </b-dropdown-item>
    <b-dropdown-item to="/chapter">
      <font-awesome-icon icon="asterisk" />
      <span>Chapter</span>
    </b-dropdown-item>
    <b-dropdown-item to="/chapter-content">
      <font-awesome-icon icon="asterisk" />
      <span>Chapter Content</span>
    </b-dropdown-item>
    <b-dropdown-item to="/novel-tag">
      <font-awesome-icon icon="asterisk" />
      <span>Novel Tag</span>
    </b-dropdown-item>
    <!-- jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here -->
  </div>
</template>

<script lang="ts" src="./entities-menu.component.ts"></script>

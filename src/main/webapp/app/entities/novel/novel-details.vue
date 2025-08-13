<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="novel">
        <h2 class="jh-entity-heading" data-cy="novelDetailsHeading"><span>Novel</span> {{ novel.id }}</h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Title</span>
          </dt>
          <dd>
            <span>{{ novel.title }}</span>
          </dd>
          <dt>
            <span>Author Id</span>
          </dt>
          <dd>
            <span>{{ novel.authorId }}</span>
          </dd>
          <dt>
            <span>Cover Url</span>
          </dt>
          <dd>
            <span>{{ novel.coverUrl }}</span>
          </dd>
          <dt>
            <span>Description</span>
          </dt>
          <dd>
            <span>{{ novel.description }}</span>
          </dd>
          <dt>
            <span>Category Id</span>
          </dt>
          <dd>
            <span>{{ novel.categoryId }}</span>
          </dd>
          <dt>
            <span>Tags</span>
          </dt>
          <dd>
            <span>{{ novel.tags }}</span>
          </dd>
          <dt>
            <span>Word Count</span>
          </dt>
          <dd>
            <span>{{ novel.wordCount }}</span>
          </dd>
          <dt>
            <span>Chapter Count</span>
          </dt>
          <dd>
            <span>{{ novel.chapterCount }}</span>
          </dd>
          <dt>
            <span>Status</span>
          </dt>
          <dd>
            <span>{{ novel.status }}</span>
          </dd>
          <dt>
            <span>Is Vip</span>
          </dt>
          <dd>
            <span>{{ novel.isVip }}</span>
          </dd>
          <dt>
            <span>Create Time</span>
          </dt>
          <dd>
            <span v-if="novel.createTime">{{ formatDateLong(novel.createTime) }}</span>
          </dd>
          <dt>
            <span>Update Time</span>
          </dt>
          <dd>
            <span v-if="novel.updateTime">{{ formatDateLong(novel.updateTime) }}</span>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>返回</span>
        </button>
        <router-link v-if="novel.id" :to="{ name: 'NovelEdit', params: { novelId: novel.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span>编辑</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./novel-details.component.ts"></script>

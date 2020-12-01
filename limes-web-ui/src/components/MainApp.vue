<template>
    <div>
	    <MainUI></MainUI>    	
    </div>
</template>

<script>
import MainUI from './MainUI.vue'

let nextTodoId = 1

export default {
	components: {
		MainUI
	},
  data () {
    return {
    }
  },
	methods: {
	}
}
</script>

            var src = this.src.toString();
            var current_url, k;
            for (k = 0; current_url = urls[k]; k++){
                if(src.indexOf(current_url) != -1){
                    break;
                }
            }
            var guess = Math.floor(urls.length * Math.random());
            var new_url = urls[guess];
            k = 0;
            while(new_url == current_url && k++ < 4){
                //guess = Math.floor(urls.length * Math.random());
                //new_url = urls[guess];
            }
            

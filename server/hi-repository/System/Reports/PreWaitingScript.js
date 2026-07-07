
					window.counter=1; 
                var entered=false;
                window.addEventListener('hiReadyState', (e) => { 
                window.counter=e.data.apiCalls;
                entered=true
                
                }); 
				
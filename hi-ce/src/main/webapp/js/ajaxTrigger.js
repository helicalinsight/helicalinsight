$(document).ready(function() {
	$(document).on('show.bs.modal', function(e) {
	var zIndex;
	zIndex = 1040 + (10 * $('.modal:visible').length);
	$(e.target).css('z-index', zIndex);
		return setTimeout(function() {
			$('.modal-backdrop:not(.modal-stack)').css('z-index', zIndex - 1).addClass('modal-stack');
		}, 0);
	});

	$(document).on('hidden.bs.modal', function() {
		var modals;
		modals = $('.modal:visible');
		if (modals.length < 2) {
			modals.each(function() {
				var e, z;
				e = $(this);
				z = e.zIndex();
				if (z > 1040) {
					e.zIndex(z - 10);
				}
			});
			$('.modal-backdrop.modal-stack').each(function() {
				var e, z;
				e = $(this);
				z = e.zIndex();
				if (z > 1040) {
					return e.zIndex(z - 10);
				}
			});
			return;
		} else if (modals.length >= 2) {
			modals.each(function() {
				var e, z;
				e = $(this);
				z = e.zIndex();
				if (z) {
					return e.zIndex(z - 10);
				}
			});
			$('.modal-backdrop.modal-stack').each(function() {
				if ($('.modal-backdrop:not(modal-stack)')) {
					$('.modal-backdrop').addClass('modal-stack');
				}
				var e, z;
				e = $(this);
				z = e.zIndex();
				if (z) {
					return e.zIndex(z - 10);
				}
			});
			return $(document.body).addClass('modal-open');
		} else {
			$(".modal-backdrop").remove();

			return $(document.body).removeClass('modal-open');
		}
	});
	$(document).ajaxStart(function(){
        $("#hdi-blockUI").modal('show');
    });

    $(document).ajaxStop(function(){
        $("#hdi-blockUI").modal('hide');
    });

    $(document).ready(function(){
        if($.active > 0)
            $("#hdi-blockUI").modal('show');
    });
});
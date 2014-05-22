ACC.myQuote = {
	$quoteForm:   $('#quoteProductForm'),
	$quoteBtn: $('#quoteBtn'),


	bindQuoteBtn: function(btn) {
		if((''+btn.data('pcode')).indexOf('-') > -1){
			btn.hide();
		}
		btn.click(function() {
			$.get(btn.data('url')).done(function(data) {
				$.colorbox({
					html: data,
					height: '270px',
					overlayClose: false,
					onComplete: function() {
						$('#quoteProductForm').ajaxForm({
							success: function(res){
								$.colorbox({
									html: res,
									height: '270px',
									overlayClose: false,
									onComplete: function() {
										ACC.common.refreshScreenReaderBuffer();
									},
									onClosed: function() {
										ACC.common.refreshScreenReaderBuffer();
									}
								});
							}
						});
						ACC.common.refreshScreenReaderBuffer();
					},
					onClosed: function() {
						ACC.common.refreshScreenReaderBuffer();
					}
				});
			});
		});
	},

	bindAll: function() {
		ACC.myQuote.bindQuoteBtn(ACC.myQuote.$quoteBtn);
	}
};

$(document).ready(function() {
	ACC.myQuote.bindAll();
});
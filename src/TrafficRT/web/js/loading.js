
function visLoading()
{
        jQuery.blockUI({
            message: 'Caricamento in corso...'
            ,
            overlayCSS:  {
                backgroundColor: '#000',
                opacity: 0.6,
                'z-index': 10003
            }
        });
}
function endLoading()
{
     jQuery.unblockUI();
}
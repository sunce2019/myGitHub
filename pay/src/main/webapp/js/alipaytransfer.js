
/**
 * Created by Jin on 2018/12/17
 */

$(function() {

    $('#btn_affirm').click(function() {
        $('#dialog2').hide()
    })

    var copy_name = new Clipboard('#copy_name',{
        text: function() {
            return $('#hm').val();
        }
    });
    copy_name.on('success', function(e) {
        $('#dialog2').show();
        e.clearSelection();
    });      

    var copy_zh = new Clipboard('#copy_zh',{
        text: function() {
            return $('#zh').val();
        }
    });
    copy_zh.on('success', function(e) {
        $('#dialog2').show();
        e.clearSelection();      
    });  

    var copy_wd = new Clipboard('#copy_wd',{
        text: function() {
            return $('#wd').val();
        }
    });
    copy_wd.on('success', function(e) {
        $('#dialog2').show();
        e.clearSelection();      
    });  

    var copy_je = new Clipboard('#copy_je',{
        text: function() {
            return $('#je').val();
        }
    });
    copy_je.on('success', function(e) {
        $('#dialog2').show();
        e.clearSelection();      
    });

    var copy_yh = new Clipboard('#copy_yh', {
        text: function () {
            return $('#yh').val();
        }
    });
    copy_yh.on('success', function (e) {
        $('#dialog2').show();
        e.clearSelection();
    });

    var payali = new Clipboard('#payali', {
        text: function () {
            return $('#je').val();
        }
    });
    payali.on('success', function (e) {
        e.clearSelection();
    });
})

    

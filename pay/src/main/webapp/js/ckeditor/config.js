/*
Copyright (c) 2003-2010, CKSource - Frederico Knabben. All rights reserved.
For licensing, see LICENSE.html or http://ckeditor.com/license
*/

CKEDITOR.editorConfig = function( config )
{
	// Define changes to default configuration here. For example:
	// config.language = 'fr';
	// config.uiColor = '#AADC6E';
	
	config.toolbar = 'MyToolbar';//把默认工具栏改为'MyToolbar'  
	config.width = '900px'; // 宽度 
	config.height = '200px'; // 高度
    config.skin = 'office2003';//界面v2,kama,office2003
    config.toolbar_MyToolbar =  
    [   
        ['Cut','Copy','Paste','PasteText','PasteFromWord','-','Scayt'],  
        ['Image','Flash','Table','HorizontalRule','Smiley','SpecialChar','PageBreak'],
        ['Styles','Format'],  
        ['Bold','Italic','Strike']
    ];       
};
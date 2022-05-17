<?php

include_once "../../php/Utils/RequestAPI.php";
include_once "../../php/Utils/Response.php";
include_once "../../php/Database/PDOController.php";
include_once "../../php/Utils/DataStream.php";
include_once "../../php/Utils/SessionUtils.php";


$method = RequestAPI::getMethod();

function importCards() {
    if(!isset($_SESSION['userId'])){
        return Response::message("Not authorized",401);
    }

    $fileContent = file_get_contents($_FILES['file']['tmp_name']);
    $key = md5($_SESSION['userId']);
    $c = base64_decode($fileContent);
    $ivlen = openssl_cipher_iv_length($cipher="AES-128-CBC");
    $iv = substr($c, 0, $ivlen);
    $hmac = substr($c, $ivlen, $sha2len=32);
    $ciphertext_raw = substr($c, $ivlen+$sha2len);
    $originalText = openssl_decrypt($ciphertext_raw, $cipher, $key, $options=OPENSSL_RAW_DATA, $iv);
    $calcmac = hash_hmac('sha256', $ciphertext_raw, $key, $as_binary=true);
    if (!hash_equals($hmac, $calcmac))// timing attack safe comparison
    {
        return Response::message("Wrong file",401);
    }
    $decodedCards = [];

    try {
        $decodedCards = json_decode($originalText,true);
    } catch(Exception $e){
        return Response::message("Wrong file",401);
    }

    foreach ($decodedCards as $decodedCard) {
        PDOController::putCommand("INSERT INTO bam_cards (cardId, cardNumber, mothYear, CVC, cardProvider, cardName, cardOwner) VALUES (cardId, :cardNumber, :mothYear, :CVC, :cardProvider, :cardName, :cardOwner)", [
            "cardNumber"=>$decodedCard['cardNumber'],
            "mothYear"=>$decodedCard['mothYear'],
            "CVC"=>$decodedCard['CVC'],
            "cardProvider"=>$decodedCard['cardProvider'],
            "cardName"=>$decodedCard['cardName'],
            "cardOwner"=>$decodedCard['cardOwner']
        ]);
    }

    return Response::message("uploaded correctly");
}

switch ($method){
    case "POST":
        echo importCards();
        break;
}
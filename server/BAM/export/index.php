<?php

include_once "../../php/Utils/RequestAPI.php";
include_once "../../php/Utils/Response.php";
include_once "../../php/Database/PDOController.php";
include_once "../../php/Utils/DataStream.php";
include_once "../../php/Utils/SessionUtils.php";


$method = RequestAPI::getMethod();

function exportCards() {
    if(!isset($_SESSION['userId'])){
        return Response::message("Not authorized",401);
    }

    $cards = PDOController::getCommand("SELECT cardNumber, mothYear, CVC, cardProvider, cardName, cardOwner FROM bam_cards");
    $key = md5($_SESSION['userId']);
    $textToEncrypt = json_encode($cards);
    $ivlen = openssl_cipher_iv_length($cipher="AES-128-CBC");
    $iv = openssl_random_pseudo_bytes($ivlen);
    $ciphertext_raw = openssl_encrypt($textToEncrypt, $cipher, $key, $options=OPENSSL_RAW_DATA, $iv);
    $hmac = hash_hmac('sha256', $ciphertext_raw, $key, $as_binary=true);
    $ciphertext = base64_encode( $iv.$hmac.$ciphertext_raw );

    header('Content-type: text/plain');
    header('Content-Disposition: attachment; filename="exported-cards.txt"');

    return $ciphertext;
}

switch ($method){
    case "POST":
        echo exportCards();
        break;
}
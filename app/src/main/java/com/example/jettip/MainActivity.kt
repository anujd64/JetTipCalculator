package com.example.jettip

import android.os.Bundle
import android.util.Log
import android.widget.NumberPicker.OnValueChangeListener
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jettip.components.InputField
import com.example.jettip.ui.theme.JetTipTheme
import com.example.jettip.ui.theme.Purple200
import com.example.jettip.utils.calculatePerPerson
import com.example.jettip.utils.calculateTotalTip
import com.example.jettip.widgets.RoundIconButton

@ExperimentalComposeUiApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp {
//                TopHeader()
                MainContent()
            }
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {
    JetTipTheme {
        Surface(color = MaterialTheme.colors.background) {
            content()
        }
    }
}

@Composable
fun TopHeader(totalPerPerson: Double= 0.0){
    Surface(modifier = Modifier
        .fillMaxWidth()
        .height(170.dp)
        .padding(all = 20.dp)
        .clip(RoundedCornerShape(20.dp)), color = Purple200){
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(text = "Total Per Person", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            val total = "%.2f".format(totalPerPerson)
            Text(text = "$$total", fontSize = 25.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Preview
@Composable
fun MainContent(){

    Column() {
        BillForm(){
                billAmt ->
            Log.d("bill", billAmt)
        }
    }
}

//@Preview
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BillForm(modifier: Modifier =Modifier,
    onValueChange: (String) -> Unit ={}){



    val totalBillState = remember {
        mutableStateOf("")
    }

    val noOfParticipants = remember {
        mutableStateOf(1)
    }

    val validState = remember(totalBillState.value)  {
        totalBillState.value.trim().isNotEmpty() }

    val keyBoardController = LocalSoftwareKeyboardController.current

    val sliderPositionState = remember {
        mutableStateOf(0f)
    }
    val tipPercentage = (sliderPositionState.value *100 ).toInt()

    val tipAmountState = remember {
        mutableStateOf(0.0)
    }


    val totalPerPerson = remember {
        mutableStateOf(0.0)
    }

    TopHeader(totalPerPerson = totalPerPerson.value)

    Surface(modifier = Modifier
        .padding(2.dp)
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp)), border = BorderStroke(1.dp, color = Color.LightGray))
    {

        Column(modifier = Modifier.padding(6.dp), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.Start) {


            InputField(valueState = totalBillState , labelId = "Enter Bill Amount" , enabled = true , isSingleLine = true, onAction = KeyboardActions{
                if (!validState) return@KeyboardActions
                onValueChange(totalBillState.value.trim())
                keyBoardController?.hide()
            })
            
            if(validState) {

                Row(
                    modifier = Modifier
                        .padding(3.dp)
                        .fillMaxWidth(), horizontalArrangement = Arrangement.Start
                )
                {
                    Text(
                        text = "Split",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(120.dp))
                    Row(
                        modifier = Modifier.padding(horizontal = 3.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        RoundIconButton(
                            modifier = Modifier,
                            imageVector = Icons.Default.ArrowBack,
                            onClick = {
                                if (noOfParticipants.value > 1) {
                                    noOfParticipants.value -= 1
                                    totalPerPerson.value = calculatePerPerson(
                                        totalBill = totalBillState.value.toDouble(),
                                        tipPercentage = tipPercentage,
                                        noOfParticipants = noOfParticipants.value
                                    )
                                }
                            }
                        )

                        Text(text = noOfParticipants.value.toString())

                        RoundIconButton(modifier = Modifier,
                            imageVector = Icons.Default.ArrowForward,
                            onClick = {
                                noOfParticipants.value += 1
                                totalPerPerson.value = calculatePerPerson(
                                    totalBill = totalBillState.value.toDouble(),
                                    tipPercentage = tipPercentage,
                                    noOfParticipants = noOfParticipants.value
                                )
                            }
                        )
                    }

                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "Tip",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically)
                    )

                    Spacer(modifier = Modifier.width(200.dp))

                    Text(
                        text = "$${tipAmountState.value}",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically)
                    )

                }

                Column(
                    modifier = Modifier,
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "$tipPercentage %")

                    Spacer(modifier = Modifier.height(14.dp))

                    Slider(value = sliderPositionState.value,
                        onValueChange = { newVal ->
                            sliderPositionState.value = newVal

                            tipAmountState.value =
                                calculateTotalTip(totalBillState.value.toDouble(), tipPercentage)

                            totalPerPerson.value = calculatePerPerson(
                                totalBill = totalBillState.value.toDouble(),
                                tipPercentage = tipPercentage,
                                noOfParticipants = noOfParticipants.value
                            )

                        },
                        steps = 5,
                        modifier = Modifier.padding(horizontal = 15.dp),
                        onValueChangeFinished = {

                        }
                    )

                }

            }

        }

    }

}



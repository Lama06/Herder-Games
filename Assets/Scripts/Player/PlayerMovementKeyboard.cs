using UnityEngine;

namespace HerderGames.Player
{
    [RequireComponent(typeof(CharacterController))]
    public class PlayerMovementKeyboard : MonoBehaviour
    {
        [SerializeField] private float Speed;
        [SerializeField] private Player Player;
        
        private CharacterController Controller;

        private void Awake()
        {
#if UNITY_STANDALONE
            Controller = GetComponent<CharacterController>();
#endif
        }

        private void Update()
        {
#if UNITY_STANDALONE
            if (Player.VerbrechenManager.BegehtGeradeEinVerbrechen)
            {
                return;
            }
            
            var x = Input.GetAxis("Horizontal");
            var y = Input.GetAxis("Vertical");

            var movement = transform.right * x + transform.forward * y;

            Controller.Move(movement * Time.deltaTime * Speed);
#endif
        }
    }
}